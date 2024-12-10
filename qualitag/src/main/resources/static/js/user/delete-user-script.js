document.getElementById('deleteAccount').addEventListener('click', function () {
  const confirmation = confirm('Are you sure you want to delete your account? This action is irreversible.');
  if (!confirmation) return;

  const authToken = localStorage.getItem('authToken');
  const urlParts = window.location.pathname.split('/');
  const username = urlParts[2]; // Extract username from URL

  fetch('/api/v1/user/' + username, {
    method: 'DELETE',
    headers: {
      'Authorization': 'Bearer ' + authToken
    }
  })
  .then(response => {
    if (response.status === 200) {
      alert('Account deleted successfully. Redirecting to homepage...');
      localStorage.clear(); // Clear user data from localStorage
      window.location.href = '/'; // Redirect to home or sign-in page
    } else {
      alert('Error deleting account. Please try again later.');
    }
  })
  .catch(error => console.error('Error:', error));
});
