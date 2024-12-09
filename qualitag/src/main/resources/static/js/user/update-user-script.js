document.getElementById('updateUserForm').addEventListener('submit', async function (event) {
  event.preventDefault();

  const authToken = localStorage.getItem('authToken');
  if (!authToken) {
    alert('Authorization token is missing. Please log in again.');
    window.location.href = '/signin';
    return;
  }

  const pathSegments = window.location.pathname.split('/');
  const username = pathSegments[pathSegments.length - 2]; // Assuming the URL is /user/{username}/update
  if (!username) {
    alert('Username is missing in the URL.');
    return;
  }

  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());

  try {
    const response = await fetch('/api/v1/user/' + encodeURIComponent(username), {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + authToken
      },
      body: JSON.stringify(data)
    });

    const responseData = await response.json();

    if (response.ok) {
      alert('User information updated successfully.');
      localStorage.setItem('authToken', responseData.token);
      localStorage.setItem('username', responseData.username);
      window.location.href = '/user/' + username;
    } else {
      alert('Update failed: ' + (responseData.msg || 'Unknown error'));
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});