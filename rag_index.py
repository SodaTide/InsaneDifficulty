"""
RAG脚本 - 使用BAAI/bge-m3模型，支持批量处理和重试
"""

import os
import json
import requests
from pathlib import Path
from typing import List, Dict, Any
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
import sys

# 配置
KEY_FILE = r"D:\Documents\embedding_key_bgem3.txt"
API_BASE = "https://api.siliconflow.cn/v1"
EMBED_URL = f"{API_BASE}/embeddings"
EMBED_MODEL = "BAAI/bge-m3"
BATCH_SIZE = 64
MAX_WORKERS = 3

RERANK_URL = "https://maas-api.cn-huabei-1.xf-yun.com/v2/rerank"
RERANK_MODEL = "xop3qwen8breranker"
RERANK_KEY_FILE = r"D:\Documents\embedding_key.txt"

# 文档目录
DOCS_DIR = r"E:\Works\AICoding\Minecraft_development_docs"
FABRIC_DOCS = os.path.join(DOCS_DIR, "fabric-docs-main")
PAPER_DOCS = os.path.join(DOCS_DIR, "paper-docs-main")

# 索引文件
INDEX_FILE = r"E:\Works\AICoding\BraveSurvival\docs_index.json"


def print_progress(current, total, prefix="", suffix="", length=50):
    """打印进度条"""
    percent = current / total
    filled = int(length * percent)
    bar = '█' * filled + '░' * (length - filled)
    sys.stdout.write(f'\r{prefix} |{bar}| {current}/{total} ({percent*100:.1f}%) {suffix}')
    sys.stdout.flush()
    if current == total:
        print()


def load_api_key(file_path: str) -> str:
    """加载API密钥"""
    with open(file_path, 'r') as f:
        return f.read().strip()


def get_embeddings_batch(texts: List[str], api_key: str) -> List[List[float]]:
    """批量获取文本的向量嵌入（带无限重试）"""
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    data = {
        "model": EMBED_MODEL,
        "input": texts,
        "encoding_format": "float"
    }
    
    while True:
        try:
            response = requests.post(EMBED_URL, headers=headers, json=data, timeout=120)
            if response.status_code == 200:
                result = response.json()
                # 按index排序确保顺序正确
                embeddings = sorted(result["data"], key=lambda x: x["index"])
                return [item["embedding"] for item in embeddings]
            elif response.status_code == 429:
                # 限流，等待后重试
                time.sleep(2)
                continue
            else:
                print(f"\nError getting embeddings: {response.status_code} - {response.text}")
                time.sleep(2)
                continue
        except Exception as e:
            print(f"\nException getting embeddings: {e}")
            time.sleep(2)
            continue


def rerank(query: str, documents: List[str], api_key: str, top_k: int = 5) -> List[Dict[str, Any]]:
    """重排序文档"""
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    data = {
        "model": RERANK_MODEL,
        "query": query,
        "documents": documents,
        "top_n": top_k
    }
    
    try:
        response = requests.post(RERANK_URL, headers=headers, json=data, timeout=30)
        if response.status_code == 200:
            result = response.json()
            return result["results"]
        else:
            return []
    except Exception as e:
        return []


def read_markdown_file(file_path: str) -> str:
    """读取Markdown文件内容"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return f.read()
    except Exception as e:
        return ""


def split_text_into_chunks(text: str, chunk_size: int = 1000, overlap: int = 200) -> List[str]:
    """将文本分割成块"""
    chunks = []
    start = 0
    while start < len(text):
        end = start + chunk_size
        chunk = text[start:end]
        if len(chunk.strip()) > 50:  # 忽略太短的块
            chunks.append(chunk)
        start = end - overlap
    return chunks


def index_documents(api_key: str, rerank_key: str) -> Dict[str, Any]:
    """索引所有文档（批量版本）"""
    index = {
        "documents": [],
        "chunks": []
    }
    
    all_chunks = []
    
    # 收集所有文档
    print("📄 收集文档...")
    
    # 索引Fabric文档
    fabric_dir = Path(FABRIC_DOCS)
    fabric_files = list(fabric_dir.rglob("*.md"))
    print(f"   找到 {len(fabric_files)} 个Fabric文档")
    
    for md_file in fabric_files:
        content = read_markdown_file(str(md_file))
        if content:
            doc_id = len(index["documents"])
            index["documents"].append({
                "id": doc_id,
                "path": str(md_file),
                "type": "fabric",
                "content": content[:500]
            })
            
            chunks = split_text_into_chunks(content)
            for i, chunk in enumerate(chunks):
                all_chunks.append((chunk, doc_id, i))
    
    # 索引Paper文档
    paper_dir = Path(PAPER_DOCS)
    paper_files = list(paper_dir.rglob("*.md")) + list(paper_dir.rglob("*.mdx"))
    print(f"   找到 {len(paper_files)} 个Paper文档")
    
    for md_file in paper_files:
        content = read_markdown_file(str(md_file))
        if content:
            doc_id = len(index["documents"])
            index["documents"].append({
                "id": doc_id,
                "path": str(md_file),
                "type": "paper",
                "content": content[:500]
            })
            
            chunks = split_text_into_chunks(content)
            for i, chunk in enumerate(chunks):
                all_chunks.append((chunk, doc_id, i))
    
    print(f"\n🔢 共收集到 {len(all_chunks)} 个块")
    print(f"   批量大小: {BATCH_SIZE}")
    print(f"   并发数: {MAX_WORKERS}")
    
    # 分批处理
    batches = []
    for i in range(0, len(all_chunks), BATCH_SIZE):
        batch = all_chunks[i:i + BATCH_SIZE]
        batches.append((i, batch))
    
    print(f"   批次数: {len(batches)}")
    print(f"\n🚀 开始批量向量化...")
    
    processed_chunks = []
    completed_batches = 0
    
    def process_batch(args):
        batch_idx, batch = args
        texts = [chunk[0] for chunk in batch]
        embeddings = get_embeddings_batch(texts, api_key)
        
        results = []
        for j, (chunk_content, doc_id, chunk_index) in enumerate(batch):
            if j < len(embeddings) and embeddings[j]:
                chunk_id = batch_idx + j
                results.append({
                    "id": chunk_id,
                    "doc_id": doc_id,
                    "chunk_index": chunk_index,
                    "content": chunk_content,
                    "embedding": embeddings[j]
                })
        return results
    
    with ThreadPoolExecutor(max_workers=MAX_WORKERS) as executor:
        futures = {executor.submit(process_batch, batch): batch[0] for batch in batches}
        
        for future in as_completed(futures):
            try:
                results = future.result()
                processed_chunks.extend(results)
            except Exception as e:
                print(f"\nError processing batch: {e}")
            completed_batches += 1
            print_progress(completed_batches, len(batches), prefix="向量化", suffix=f"块数: {len(processed_chunks)}")
    
    index["chunks"] = processed_chunks
    return index


def save_index(index: Dict[str, Any]):
    """保存索引到文件"""
    with open(INDEX_FILE, 'w', encoding='utf-8') as f:
        json.dump(index, f, ensure_ascii=False, indent=2)
    print(f"\n💾 索引已保存到 {INDEX_FILE}")


def load_index() -> Dict[str, Any]:
    """加载索引"""
    if os.path.exists(INDEX_FILE):
        with open(INDEX_FILE, 'r', encoding='utf-8') as f:
            return json.load(f)
    return {"documents": [], "chunks": []}


def cosine_similarity(vec1: List[float], vec2: List[float]) -> float:
    """计算余弦相似度"""
    if not vec1 or not vec2:
        return 0.0
    
    dot_product = sum(a * b for a, b in zip(vec1, vec2))
    norm1 = sum(a * a for a in vec1) ** 0.5
    norm2 = sum(b * b for b in vec2) ** 0.5
    
    if norm1 == 0 or norm2 == 0:
        return 0.0
    
    return dot_product / (norm1 * norm2)


def search_documents(query: str, api_key: str, rerank_key: str, top_k: int = 5) -> List[Dict[str, Any]]:
    """搜索文档"""
    index = load_index()
    
    if not index["chunks"]:
        print("❌ 索引为空，请先运行索引")
        return []
    
    print(f"🔍 搜索: {query}")
    
    # 获取查询的向量嵌入
    query_embedding = get_embeddings_batch([query], api_key)[0]
    if not query_embedding:
        return []
    
    # 计算相似度
    print("   计算相似度...")
    similarities = []
    for chunk in index["chunks"]:
        sim = cosine_similarity(query_embedding, chunk["embedding"])
        similarities.append({
            "chunk_id": chunk["id"],
            "similarity": sim,
            "content": chunk["content"],
            "doc_id": chunk["doc_id"]
        })
    
    # 按相似度排序
    similarities.sort(key=lambda x: x["similarity"], reverse=True)
    
    # 获取top_k结果
    top_results = similarities[:top_k * 2]
    
    # 重排序
    print("   重排序...")
    documents = [r["content"] for r in top_results]
    reranked = rerank(query, documents, rerank_key, top_k)
    
    # 构建最终结果
    results = []
    for r in reranked:
        idx = r["index"]
        if idx < len(top_results):
            result = top_results[idx]
            doc = index["documents"][result["doc_id"]]
            results.append({
                "content": result["content"],
                "path": doc["path"],
                "type": doc["type"],
                "score": r["relevance_score"]
            })
    
    return results


def main():
    """主函数"""
    if len(sys.argv) < 2:
        print("用法:")
        print("  python rag_index.py index    # 索引文档")
        print("  python rag_index.py search <query>  # 搜索文档")
        return
    
    api_key = load_api_key(KEY_FILE)
    rerank_key = load_api_key(RERANK_KEY_FILE)
    
    command = sys.argv[1]
    
    if command == "index":
        print("🚀 开始索引文档...")
        start_time = time.time()
        index = index_documents(api_key, rerank_key)
        save_index(index)
        elapsed = time.time() - start_time
        print(f"\n✅ 索引完成!")
        print(f"   文档数: {len(index['documents'])}")
        print(f"   块数: {len(index['chunks'])}")
        print(f"   耗时: {elapsed:.2f} 秒")
    
    elif command == "search":
        if len(sys.argv) < 3:
            print("请提供搜索查询")
            return
        
        query = " ".join(sys.argv[2:])
        results = search_documents(query, api_key, rerank_key)
        
        if results:
            print(f"\n📋 找到 {len(results)} 个结果:")
            for i, result in enumerate(results, 1):
                print(f"\n{i}. [{result['type']}] {result['path']}")
                print(f"   分数: {result['score']:.4f}")
                print(f"   内容: {result['content'][:200]}...")
        else:
            print("❌ 未找到相关结果")
    
    else:
        print(f"未知命令: {command}")


if __name__ == "__main__":
    main()
