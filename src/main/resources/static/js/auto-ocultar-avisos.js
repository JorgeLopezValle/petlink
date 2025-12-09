document.addEventListener('DOMContentLoaded', function() {
    const errorMessages = document.querySelectorAll('.bg-red-50.border-red-500');
    const successMessages = document.querySelectorAll('.bg-teal-50.border-teal-500');

    const allMessages = [...errorMessages, ...successMessages];

    allMessages.forEach(function(message) {
        setTimeout(function() {
            message.style.transition = 'opacity 0.5s ease-out';
            message.style.opacity = '0';

            setTimeout(function() {
                message.style.display = 'none';
            }, 500);
        }, 5000);
    });
});
