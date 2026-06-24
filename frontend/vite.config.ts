import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true
  },
  server: {
    port: 5173,
    proxy: {
      '/auth': 'http://localhost:8080',
      '/delishies': 'http://localhost:8080',
      '/products': 'http://localhost:8080',
      '/users': 'http://localhost:8080',
      '/me': 'http://localhost:8080',
      '/groups': 'http://localhost:8080',
      '/feedbacks': 'http://localhost:8080',
      '/favorite-delishies': 'http://localhost:8080',
      '/cuisines': 'http://localhost:8080',
      '/me/profile': 'http://localhost:8080',
      '/me/preferences': 'http://localhost:8080',
      '/me/meal-history': 'http://localhost:8080',
      '/me/meal-plans': 'http://localhost:8080',
    },
  },
})
