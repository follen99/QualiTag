/**
 * Refreshes the user data stored in the local storage.
 */
export async function refreshUserData() {
    try {
      const response = await fetch(`/api/v1/user/${localStorage.getItem('username')}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });
  
      const responseData = await response.json();
  
      if (response.ok) {
        const user = responseData;
  
        localStorage.setItem('username', user.username);
        localStorage.setItem('user-email', user.email);
        localStorage.setItem('user-projectRoles', JSON.stringify(user.projectRoles));
        localStorage.setItem('user-name', user.name);
        localStorage.setItem('user-surname', user.surname);
        localStorage.setItem('user-projectIds', JSON.stringify(user.projectIds));
        localStorage.setItem('user-tagIds', JSON.stringify(user.teamIds));
        localStorage.setItem('user-lastFetch', Date.now());
      } else {
        console.error('Failed to refresh user data:', responseData.message);
      }
    } catch (error) {
      console.error('Error refreshing user data:', error);
    }
    window.location.reload();
  }
