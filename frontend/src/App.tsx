import { useState } from 'react'
import type { FormEvent } from 'react'
import './App.css'

interface RegistrationResponse {
  id: number
  firstName: string
  lastName: string
  email: string
  createdAt: string
}

function App() {
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
  })

  const [loading, setLoading] = useState(false)
  const [success, setSuccess] = useState('')
  const [error, setError] = useState('')

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [event.target.name]: event.target.value,
    })
  }

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault()

    setLoading(true)
    setSuccess('')
    setError('')

    try {
      const response = await fetch('/api/candidates', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(form),
      })

      if (!response.ok) {
        throw new Error(`Registration failed: ${response.status}`)
      }

      const data: RegistrationResponse = await response.json()

      setSuccess(
        `Registration successful! Welcome ${data.firstName} ${data.lastName}.`
      )

      setForm({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
      })
    } catch (err) {
      console.error(err)
      setError(
        'Registration failed. Please make sure the backend is running.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app">
      <div className="registration-card">
        <h1>Cloud Campus</h1>

        <h2>Candidate Registration</h2>

        <p className="subtitle">
          Create your account and start applying for opportunities.
        </p>

        <form onSubmit={handleSubmit}>
          <div className="name-row">
            <div className="form-group">
              <label htmlFor="firstName">First Name</label>

              <input
                id="firstName"
                name="firstName"
                type="text"
                value={form.firstName}
                onChange={handleChange}
                placeholder="John"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="lastName">Last Name</label>

              <input
                id="lastName"
                name="lastName"
                type="text"
                value={form.lastName}
                onChange={handleChange}
                placeholder="Doe"
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              placeholder="john@example.com"
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">Password</label>

            <input
              id="password"
              name="password"
              type="password"
              value={form.password}
              onChange={handleChange}
              placeholder="Minimum 8 characters"
              minLength={8}
              required
            />
          </div>

          {success && <div className="success-message">{success}</div>}

          {error && <div className="error-message">{error}</div>}

          <button type="submit" disabled={loading}>
            {loading ? 'Creating Account...' : 'Register'}
          </button>
        </form>

        <p className="login-text">
          Already have an account? <span>Login</span>
        </p>
      </div>
    </div>
  )
}

export default App
