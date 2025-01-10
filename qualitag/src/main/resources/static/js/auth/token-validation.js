document.addEventListener('DOMContentLoaded', async function () {
  const authToken = localStorage.getItem('authToken');

  if (!authToken) {
    alert('You must be logged in to access this page.');
    window.location.href = '/signin'; // Redirect to login page
  }

  const response = await fetch('/api/v1/auth/check-token', {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer ' + authToken
    }
  });

  console.log(response);

  if (!response.ok) {
    alert('Your session has expired. Please log in again.');
    localStorage.clear(); // Clear all items in local storage


    window.location.href = '/signin'; // Redirect to login page
  }
});