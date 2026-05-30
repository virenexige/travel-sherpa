import type { McpContext, PriceHistoryPoint, Recommendation, SearchActivityLog, SearchResult, TravelWatch, TravelWatchRequest } from './types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
const TOKEN_KEY = 'smart-travel-token';

async function getToken(): Promise<string> {
  const cached = localStorage.getItem(TOKEN_KEY);
  if (cached) return cached;
  const response = await fetch(`${API_BASE_URL}/api/auth/demo-token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email: 'demo@example.com', name: 'Demo Traveller' })
  });
  if (!response.ok) throw new Error('Unable to create demo session');
  const data = (await response.json()) as { token: string };
  localStorage.setItem(TOKEN_KEY, data.token);
  return data.token;
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const token = await getToken();
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
      ...options.headers
    }
  });
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed with ${response.status}`);
  }
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}

export const api = {
  listWatches: () => request<TravelWatch[]>('/api/travel-watches'),
  createWatch: (payload: TravelWatchRequest) =>
    request<TravelWatch>('/api/travel-watches', { method: 'POST', body: JSON.stringify(payload) }),
  getWatch: (id: string) => request<TravelWatch>(`/api/travel-watches/${id}`),
  pauseWatch: (id: string) => request<TravelWatch>(`/api/travel-watches/${id}/pause`, { method: 'PATCH' }),
  resumeWatch: (id: string) => request<TravelWatch>(`/api/travel-watches/${id}/resume`, { method: 'PATCH' }),
  deleteWatch: (id: string) => request<void>(`/api/travel-watches/${id}`, { method: 'DELETE' }),
  searchNow: (id: string) => request<SearchResult[]>(`/api/travel-watches/${id}/search-now`, { method: 'POST' }),
  results: (id: string) => request<SearchResult[]>(`/api/travel-watches/${id}/results`),
  priceHistory: (id: string) => request<PriceHistoryPoint[]>(`/api/travel-watches/${id}/price-history`),
  recommendations: (id: string) => request<Recommendation[]>(`/api/travel-watches/${id}/recommendations`),
  mcpContext: (id: string) => request<McpContext>(`/api/travel-watches/${id}/mcp-context`),
  searchLogs: (id: string) => request<SearchActivityLog[]>(`/api/travel-watches/${id}/search-logs`)
};
