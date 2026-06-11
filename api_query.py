"""
Minecraft API查询脚本
用于快速查找Fabric/Paper API的使用方法
"""

import os
import json
import requests
from typing import List, Dict, Any

# 配置
KEY_FILE = r"D:\Documents\embedding_key.txt"
EMBED_URL = "https://maas-api.cn-huabei-1.xf-yun.com/v2/embeddings"
EMBED_MODEL = "xop3qwen8bembedding"

RERANK_URL = "https://maas-api.cn-huabei-1.xf-yun.com/v2/rerank"
RERANK_MODEL = "xop3qwen8breranker"

INDEX_FILE = r"E:\Works\AICoding\BraveSurvival\docs_index.json"


def load_api_key() -> str:
    """加载API密钥"""
    with open(KEY_FILE, 'r') as f:
        return f.read().strip()


def get_embedding(text: str, api_key: str) -> List[float]:
    """获取文本的向量嵌入"""
    headers = {
        "Authorization": f"Bearer {api_key}",
        "Content-Type": "application/json"
    }
    
    data = {
        "model": EMBED_MODEL,
        "input": text,
        "input_type": "passage"
    }
    
    response = requests.post(EMBED_URL, headers=headers, json=data)
    if response.status_code == 200:
        result = response.json()
        return result["data"][0]["embedding"]
    else:
        print(f"Error getting embedding: {response.status_code}")
        return []


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
    
    response = requests.post(RERANK_URL, headers=headers, json=data)
    if response.status_code == 200:
        result = response.json()
        return result["results"]
    else:
        print(f"Error in rerank: {response.status_code}")
        return []


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


def load_index() -> Dict[str, Any]:
    """加载索引"""
    if os.path.exists(INDEX_FILE):
        with open(INDEX_FILE, 'r', encoding='utf-8') as f:
            return json.load(f)
    return {"documents": [], "chunks": []}


def search(query: str, top_k: int = 5) -> List[Dict[str, Any]]:
    """搜索文档"""
    api_key = load_api_key()
    index = load_index()
    
    if not index["chunks"]:
        print("索引为空，请先运行 python rag_index.py index")
        return []
    
    # 获取查询的向量嵌入
    query_embedding = get_embedding(query, api_key)
    if not query_embedding:
        return []
    
    # 计算相似度
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
    documents = [r["content"] for r in top_results]
    reranked = rerank(query, documents, api_key, top_k)
    
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


# 预定义的API查询
QUERIES = {
    "entity_damage": "如何监听实体受伤事件 EntityDamageEvent",
    "entity_attack": "如何监听实体攻击事件 EntityDamageByEntityEvent",
    "player_move": "如何监听玩家移动事件 PlayerMoveEvent",
    "player_bed": "如何监听玩家睡觉事件 PlayerBedEnterEvent",
    "weather_change": "如何监听天气变化事件 WeatherChangeEvent",
    "portal": "如何监听传送门使用事件",
    "equipment": "如何修改实体装备生成",
    "loot_table": "如何修改战利品表掉落物",
    "recipe": "如何修改配方 crafting recipe",
    "attribute": "如何修改实体属性 EntityAttributes",
    "effect": "如何添加状态效果 StatusEffect",
    "spawn": "如何监听实体生成事件 CreatureSpawnEvent",
    "block_break": "如何监听方块破坏事件 BlockBreakEvent",
    "mixin_inject": "如何使用Mixin注入到方法",
    "mixin_redirect": "如何使用Mixin重定向方法调用",
    "mixin_modify": "如何使用Mixin修改变量",
}


def query_api(api_name: str, top_k: int = 3) -> List[Dict[str, Any]]:
    """查询特定API"""
    if api_name in QUERIES:
        query = QUERIES[api_name]
        print(f"查询: {query}")
        return search(query, top_k)
    else:
        print(f"未知API: {api_name}")
        print(f"可用的API: {', '.join(QUERIES.keys())}")
        return []


def main():
    """主函数"""
    import sys
    
    if len(sys.argv) < 2:
        print("用法:")
        print("  python api_query.py <query>  # 自由搜索")
        print("  python api_query.py api <api_name>  # 查询特定API")
        print(f"\n可用的API: {', '.join(QUERIES.keys())}")
        return
    
    if sys.argv[1] == "api":
        if len(sys.argv) < 3:
            print("请提供API名称")
            return
        
        api_name = sys.argv[2]
        results = query_api(api_name)
    else:
        query = " ".join(sys.argv[1:])
        results = search(query)
    
    if results:
        print(f"\n找到 {len(results)} 个结果:")
        for i, result in enumerate(results, 1):
            print(f"\n{i}. [{result['type']}] {result['path']}")
            print(f"   分数: {result['score']:.4f}")
            print(f"   内容: {result['content'][:300]}...")
    else:
        print("未找到相关结果")


if __name__ == "__main__":
    main()
