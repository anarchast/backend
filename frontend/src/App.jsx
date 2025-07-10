import React, { useState } from 'react';

function App() {
  const [jobId, setJobId] = useState(null);
  const [status, setStatus] = useState(null);
  const [downloadUrl, setDownloadUrl] = useState(null);

  const startExport = async () => {
    const res = await fetch('http://localhost:8080/api/export/start', { method: 'POST' });
    const data = await res.json();
    setJobId(data.jobId);
    pollStatus(data.jobId);
  };

  const pollStatus = (id) => {
    const interval = setInterval(async () => {
      const res = await fetch(`http://localhost:8080/api/export/status/${id}`);
      const data = await res.json();
      setStatus(data.status);
      if (data.status === 'DONE') {
        setDownloadUrl(`http://localhost:8080/api/export/download/${id}`);
        clearInterval(interval);
      }
    }, 2000);
  };

  return (
    <div style={{ padding: '2rem' }}>
      <button onClick={startExport}>Export starten</button>
      {status && <p>Status: {status}</p>}
      {downloadUrl && <a href={downloadUrl}>Download</a>}
    </div>
  );
}

export default App;