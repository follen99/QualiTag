document.addEventListener('DOMContentLoaded', function () {
  const username = localStorage.getItem('username');
  const authToken = localStorage.getItem('authToken');

  if (username) {
    fetch('/api/v1/user/' + username, {
      headers: {
        'Authorization': 'Bearer ' + authToken
      }
    })
    .then(response => response.json())
    .then(user => {
      console.log('Fetched user data:', user); // Log the user object
      if (user) {
        document.getElementById('username').value = user.username || '';
        document.getElementById('email').value = user.email || '';
        document.getElementById('name').value = user.name || '';
        document.getElementById('surname').value = user.surname || '';
      } else {
        alert('User data is null or undefined');
      }
    })
    .catch(error => console.error('Error fetching user data:', error));
  } else {
    alert('No user logged in');
    window.location.href = '/signin';
  }
});

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

      const username = localStorage.getItem('username');
      window.location.href = '/user/' + username;
    } else {
      alert('Update failed: ' + (responseData.msg || 'Unknown error'));
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});