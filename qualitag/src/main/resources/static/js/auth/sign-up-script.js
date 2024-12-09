document.getElementById('registrationForm').addEventListener('submit', async function(event) {
  event.preventDefault();

  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());

  try {
    const response = await fetch('/api/v1/auth/register', {
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
      localStorage.setItem('username', responseData.username);
      alert(responseData.msg || 'Registration successful');
      window.location.href = '/'; // Redirect to homepage after registration
    } else {
      // Error: Display the error message from the JSON response
      alert('Registration failed: ' + (responseData.msg || 'Unknown error'));
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});