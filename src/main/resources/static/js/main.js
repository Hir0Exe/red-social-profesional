// Main JavaScript file for Red Profesional

// API base URL
const API_BASE_URL = '/api';

// Get JWT token from localStorage
function getAuthToken() {
    return localStorage.getItem('token');
}

// Set authorization header for API requests
function getAuthHeaders() {
    const token = getAuthToken();
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

// Check if user is authenticated
function isAuthenticated() {
    return !!getAuthToken();
}

// Get current user from localStorage
function getCurrentUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

// Logout function
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = '/login';
}

// Format date to Spanish locale
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('es-ES', options);
}

// Show toast notification
function showToast(message, type = 'info') {
    // Create toast container if it doesn't exist
    let toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toastContainer';
        toastContainer.className = 'position-fixed top-0 end-0 p-3';
        toastContainer.style.zIndex = '1050';
        document.body.appendChild(toastContainer);
    }
    
    // Create toast element
    const toastId = 'toast-' + Date.now();
    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center text-white bg-${type} border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    // Initialize and show toast
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();
    
    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// Handle API errors
function handleApiError(error) {
    console.error('API Error:', error);
    if (error.status === 401) {
        // Unauthorized - redirect to login
        logout();
    } else if (error.message) {
        showToast(error.message, 'danger');
    } else {
        showToast('Ha ocurrido un error. Por favor intenta de nuevo.', 'danger');
    }
}

// Load user profile in navbar
function loadNavbarProfile() {
    const user = getCurrentUser();
    if (user && document.querySelector('.navbar')) {
        // Update user dropdown if it exists
        const userDropdown = document.getElementById('userDropdown');
        if (userDropdown) {
            const userName = userDropdown.querySelector('span');
            if (userName) {
                userName.textContent = user.nombre;
            }
        }
    }
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Load navbar profile if authenticated
    if (isAuthenticated()) {
        loadNavbarProfile();
    }
    
    // Initialize Bootstrap tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Handle logout form if present
    const logoutForm = document.querySelector('form[action="/logout"]');
    if (logoutForm) {
        logoutForm.addEventListener('submit', function(e) {
            e.preventDefault();
            logout();
        });
    }
});

// Export functions for use in other scripts
window.RedProfesional = {
    getAuthToken,
    getAuthHeaders,
    isAuthenticated,
    getCurrentUser,
    logout,
    formatDate,
    showToast,
    handleApiError
}; 