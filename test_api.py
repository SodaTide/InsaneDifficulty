"""测试API速度"""
import requests
import time

# 读取API密钥
with open(r'D:\Documents\embedding_key.txt', 'r') as f:
    api_key = f.read().strip()

headers = {
    'Authorization': f'Bearer {api_key}',
    'Content-Type': 'application/json'
}

# 测试单个嵌入
data = {
    'model': 'xop3qwen8bembedding',
    'input': '如何监听实体受伤事件',
    'input_type': 'passage'
}

start = time.time()
response = requests.post('https://maas-api.cn-huabei-1.xf-yun.com/v2/embeddings', headers=headers, json=data, timeout=30)
elapsed = time.time() - start

print(f'状态码: {response.status_code}')
print(f'耗时: {elapsed:.2f}秒')
if response.status_code == 200:
    result = response.json()
    embedding = result['data'][0]['embedding']
    print(f'嵌入维度: {len(embedding)}')
    print('API正常工作!')
else:
    print(f'错误: {response.text}')
