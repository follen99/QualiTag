document.getElementById('updatePasswordForm').addEventListener('submit', async function (event) {
  event.preventDefault();

  const authToken = localStorage.getItem('authToken');
  if (!authToken) {
    alert('Authorization token is missing. Please log in again.');
    window.location.href = '/signin';
    return;
  }

  // Extract username from URL
  const urlParts = window.location.pathname.split('/');
  const username = urlParts[2];

  if (!username) {
    alert('Username is missing in the URL.');
    return;
  }

  const formData = new FormData(event.target);
  const data = Object.fromEntries(formData.entries());

  try {
    const response = await fetch('/api/v1/user/' + encodeURIComponent(username) + '/password', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + authToken
      },
      body: JSON.stringify(data)
    });

    const responseData = await response.json();

    if (response.ok) {
      alert(responseData.msg || 'Password updated successfully.');
      window.location.href = '/user/' + username;
    } else {
      alert('Update failed: ' + (responseData.msg || 'Unknown error'));
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});