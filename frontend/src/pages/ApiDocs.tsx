import { useState } from 'react'
import { API_MODULES } from '../apiDocs'

const METHOD_COLORS: Record<string, string> = {
  GET: 'method-get',
  POST: 'method-post',
  PUT: 'method-put',
  PATCH: 'method-patch',
  DELETE: 'method-delete',
}

export default function ApiDocs() {
  const [openModule, setOpenModule] = useState<string | null>(API_MODULES[0]?.id ?? null)
  const [copied, setCopied] = useState<string | null>(null)

  const copyPath = async (method: string, path: string) => {
    const text = `${method} ${path}`
    try {
      await navigator.clipboard.writeText(text)
      setCopied(text)
      setTimeout(() => setCopied(null), 1500)
    } catch {
      /* ignore */
    }
  }

  return (
    <section className="planner-page api-docs-page">
      <h1 className="page-title">REST API</h1>
      <p className="hint api-docs-intro">
        Модули backend-сервиса FoodIsEasy. Базовый URL: <code>/</code> (тот же хост, что и UI).
        Защищённые методы требуют JWT в заголовке <code>Authorization: Bearer …</code>.
      </p>

      {copied && <div className="toast success">Скопировано: {copied}</div>}

      <div className="api-modules">
        {API_MODULES.map(mod => (
          <article key={mod.id} className="profile-card api-module">
            <button
              type="button"
              className="api-module-header"
              onClick={() => setOpenModule(openModule === mod.id ? null : mod.id)}
            >
              <span>
                <strong>{mod.title}</strong>
                <span className="hint"> — {mod.description}</span>
              </span>
              <span className="api-module-toggle">{openModule === mod.id ? '−' : '+'}</span>
            </button>

            {openModule === mod.id && (
              <ul className="api-endpoint-list">
                {mod.endpoints.map(ep => (
                  <li key={`${ep.method}-${ep.path}`} className="api-endpoint">
                    <div className="api-endpoint-row">
                      <span className={`api-method ${METHOD_COLORS[ep.method]}`}>{ep.method}</span>
                      <code className="api-path">{ep.path}</code>
                      {ep.auth && <span className="api-auth-badge">JWT</span>}
                      <button
                        type="button"
                        className="btn-link api-copy"
                        onClick={() => copyPath(ep.method, ep.path)}
                      >
                        Копировать
                      </button>
                    </div>
                    <p className="api-desc">{ep.description}</p>
                    {ep.body && (
                      <pre className="api-body">{ep.body}</pre>
                    )}
                  </li>
                ))}
              </ul>
            )}
          </article>
        ))}
      </div>
    </section>
  )
}
