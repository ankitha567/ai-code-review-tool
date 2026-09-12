import { useState, useEffect, useRef } from 'react';
import Editor from '@monaco-editor/react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

function App() {
  const [code, setCode] = useState(
    `public class Test {\n    public void foo() {\n        int x = 5;\n    }\n}`
  );
  const [issues, setIssues] = useState([]);
  const [loading, setLoading] = useState(false);
  const [explanations, setExplanations] = useState({});
  const [explainLoading, setExplainLoading] = useState({});
  const [roomId, setRoomId] = useState('room1');
  const [connected, setConnected] = useState(false);

  const stompClientRef = useRef(null);
  const isRemoteUpdate = useRef(false);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws');
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/topic/room/${roomId}`, (message) => {
          const body = JSON.parse(message.body);
          isRemoteUpdate.current = true;
          setCode(body.code);
        });
      },
      onDisconnect: () => setConnected(false),
    });
    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [roomId]);

  const handleCodeChange = (value) => {
    setCode(value);
    if (isRemoteUpdate.current) {
      isRemoteUpdate.current = false;
      return;
    }
    if (stompClientRef.current && stompClientRef.current.connected) {
      stompClientRef.current.publish({
        destination: '/app/code-update',
        body: JSON.stringify({ code: value, roomId }),
      });
    }
  };

  const handleReview = async () => {
    setLoading(true);
    setExplanations({});
    try {
      const res = await axios.post('http://localhost:8080/api/analyze', { code });
      setIssues(res.data);
    } catch (err) {
      console.error(err);
      setIssues([{ line: 0, rule: 'Error', message: err.message, severity: 'High' }]);
    }
    setLoading(false);
  };

  const handleExplain = async (issue, idx) => {
    setExplainLoading((prev) => ({ ...prev, [idx]: true }));
    try {
      const res = await axios.post('http://localhost:8080/api/explain', {
        code,
        rule: issue.rule,
        issueMessage: issue.message,
      });
      setExplanations((prev) => ({ ...prev, [idx]: res.data }));
    } catch (err) {
      setExplanations((prev) => ({ ...prev, [idx]: 'Error fetching explanation: ' + err.message }));
    }
    setExplainLoading((prev) => ({ ...prev, [idx]: false }));
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif' }}>
      <h1>Code Review Tool</h1>

      <div style={{ marginBottom: '10px' }}>
        <label>Room: </label>
        <input
          value={roomId}
          onChange={(e) => setRoomId(e.target.value)}
          style={{ padding: '5px', marginRight: '10px' }}
        />
        <span style={{ color: connected ? 'lightgreen' : 'red' }}>
          {connected ? '● Connected' : '● Disconnected'}
        </span>
      </div>

      <Editor
        height="400px"
        defaultLanguage="java"
        value={code}
        onChange={handleCodeChange}
        theme="vs-dark"
      />

      <button
        onClick={handleReview}
        disabled={loading}
        style={{ marginTop: '15px', padding: '10px 20px', cursor: 'pointer' }}
      >
        {loading ? 'Reviewing...' : 'Review Code'}
      </button>

      <div style={{ marginTop: '20px' }}>
        <h3>Issues Found: {issues.length}</h3>
        {issues.map((issue, idx) => (
          <div
            key={idx}
            style={{
              border: '1px solid #ccc',
              borderRadius: '6px',
              padding: '10px',
              marginBottom: '10px',
              backgroundColor: issue.severity === 'High' ? '#ffe6e6' : '#fff8e6',
            }}
          >
            <strong>Line {issue.line}</strong> — <em>{issue.rule}</em>
            <p style={{ margin: '5px 0 0' }}>{issue.message}</p>
            <small>Severity: {issue.severity}</small>
            <br />
            <button
              onClick={() => handleExplain(issue, idx)}
              disabled={explainLoading[idx]}
              style={{ marginTop: '8px', padding: '6px 12px', cursor: 'pointer' }}
            >
              {explainLoading[idx] ? 'Explaining...' : 'Explain with AI'}
            </button>

            {explanations[idx] && (
              <div
                style={{
                  marginTop: '10px',
                  padding: '10px',
                  backgroundColor: '#eef4ff',
                  borderRadius: '6px',
                }}
              >
                <ReactMarkdown>{explanations[idx]}</ReactMarkdown>
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;