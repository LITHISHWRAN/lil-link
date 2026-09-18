import { useState } from "react";
import axios from "axios";
import "./App.css";

function App() {

  const [url, setUrl] = useState("");
  const [expiresAt, setExpiresAt] = useState("");
  const [result, setResult] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [error, setError] = useState("");

  const shortenUrl = async () => {

    try {
      setError("");

      const response = await axios.post(
        "http://localhost:8080/api/v1/urls",
        {
          originalUrl: url,
          expiresAt: expiresAt
            ? new Date(expiresAt).toISOString()
            : null
        }
      );

      setResult(response.data);
    } catch (err) {
      setError(
        err.response?.data?.error ||
        err.response?.data ||
        "Something went wrong"
      );
    }
  };

  const loadAnalytics = async () => {

    if (!result) return;

    const response = await axios.get(
      `http://localhost:8080/api/v1/urls/${result.shortCode}/analytics`
    );

    setAnalytics(response.data);
  };

  return (
    <div className="container">

      <h1>Distributed URL Shortener</h1>

      <p>
        Create fast, reliable and trackable short URLs.
      </p>

      <div className="card">

        <label>Original URL</label>

        <input
          type="text"
          placeholder="https://example.com/very/long/url"
          value={url}
          onChange={(e) => setUrl(e.target.value)}
        />

        <label>Expiration</label>

        <input
          type="datetime-local"
          value={expiresAt}
          onChange={(e) => setExpiresAt(e.target.value)}
        />

        <button onClick={shortenUrl}>
          Shorten URL
        </button>

        {error && (
          <div className="error">
            {error}
          </div>
        )}

      </div>

      {result && (
        <div className="card">

          <h2>Your Short URL</h2>

          <a
            href={result.shortUrl}
            target="_blank"
            rel="noreferrer"
          >
            {result.shortUrl}
          </a>

          <button onClick={loadAnalytics}>
            View Analytics
          </button>

        </div>
      )}

      {analytics && (
        <div className="card">

          <h2>Analytics</h2>

          <div className="stats">
            <div>
              <strong>{analytics.clickCount}</strong>
              <span>Total Clicks</span>
            </div>

            <div>
              <strong>{analytics.shortCode}</strong>
              <span>Short Code</span>
            </div>
          </div>

        </div>
      )}

    </div>
  );
}

export default App;