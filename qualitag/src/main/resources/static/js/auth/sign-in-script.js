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
      /*localStorage.setItem('authToken', responseData.token);
      localStorage.setItem('username', responseData.username);
      localStorage.setItem('user-email', responseData.email);
      localStorage.setItem('user-roles', responseData.roles);
      localStorage.setItem('user-firstname', responseData.firstname);
      localStorage.setItem('user-lastname', responseData.lastname);
      localStorage.setItem('user-projectIds', JSON.stringify(responseData.projectIds));
      localStorage.setItem('user-tagIds', JSON.stringify(responseData.teamIds));*/

      localStorage.setItem('authToken', responseData.token);

      const user = responseData.user;
      console.log(JSON.stringify(user, null, 2));

      localStorage.setItem('username', user.username);
      localStorage.setItem('user-email', user.email);
      localStorage.setItem('user-projectRoles', JSON.stringify(user.projectRoles));
      localStorage.setItem('user-name', user.name);
      localStorage.setItem('user-surname', user.surname);
      localStorage.setItem('user-projectIds', JSON.stringify(user.projectIds));
      localStorage.setItem('user-tagIds', JSON.stringify(user.teamIds));
      localStorage.setItem('user-lastFetch', Date.now());
      localStorage.setItem('user-userId', user.userId);

      console.log(JSON.stringify(localStorage, null, 2));


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
