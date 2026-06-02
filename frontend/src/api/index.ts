const BASE = ''

async function request(url: string, options: RequestInit = {}) {
  const headers: any = { 'Content-Type': 'application/json', ...options.headers }

  const res = await fetch(BASE + url, { ...options, headers, credentials: 'same-origin' })
  const json = await res.json()
  if (json.code !== 200) throw new Error(json.message || '\u8BF7\u6C42\u5931\u8D25')
  return json
}

export const api = {
  get: (url: string) => request(url),
  post: (url: string, data?: any) => request(url, { method: 'POST', body: data ? JSON.stringify(data) : undefined }),
  put: (url: string, data?: any) => request(url, { method: 'PUT', body: data ? JSON.stringify(data) : undefined }),
  delete: (url: string) => request(url, { method: 'DELETE' }),
}