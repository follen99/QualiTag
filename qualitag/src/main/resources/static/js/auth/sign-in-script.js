document.getElementById('loginForm').addEventListener('submit', async function(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());

  try {
    const response = await fetch('/api/v1/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    });

    const responseData = await response.json(); // Parse as JSON

    if (response.ok) {
      // Success: Use the token from the JSON response
      localStorage.setItem('authToken', responseData.token);
      alert(responseData.msg || 'Login successful');
      window.location.href = '/'; // Redirect to homepage after login
    } else {
      // Error: Display the error message from the JSON response
      alert('Login failed: ' + (responseData.msg || 'Unknown error'));
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});