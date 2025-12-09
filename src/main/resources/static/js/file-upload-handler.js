(function() {
    'use strict';

    function inicializarFileUpload(dropZone) {
        const fileInputId = dropZone.getAttribute('data-file-input');
        const fileListId = dropZone.getAttribute('data-file-list');

        const fileInput = document.getElementById(fileInputId);
        const fileList = document.getElementById(fileListId);

        if (!fileInput || !fileList) {
            console.error('No se encontraron los elementos necesarios para el file upload');
            return;
        }

        dropZone.addEventListener('click', () => fileInput.click());

        fileInput.addEventListener('change', function () {
            updateFileList(this.files);
        });

        dropZone.addEventListener('dragover', (e) => {
            e.preventDefault();
            dropZone.classList.add('border-blue-500', 'bg-blue-50');
            dropZone.classList.remove('border-gray-300', 'bg-gray-50', 'border-red-500', 'bg-red-50');
        });

        dropZone.addEventListener('dragleave', () => {
            dropZone.classList.remove('border-blue-500', 'bg-blue-50');
            const hasError = dropZone.classList.contains('bg-red-50');
            if (hasError) {
                dropZone.classList.add('border-red-500', 'bg-red-50');
            } else {
                dropZone.classList.add('border-gray-300', 'bg-gray-50');
            }
        });

        dropZone.addEventListener('drop', (e) => {
            e.preventDefault();
            dropZone.classList.remove('border-blue-500', 'bg-blue-50');
            const hasError = dropZone.classList.contains('bg-red-50');
            if (hasError) {
                dropZone.classList.add('border-red-500', 'bg-red-50');
            } else {
                dropZone.classList.add('border-gray-300', 'bg-gray-50');
            }
            fileInput.files = e.dataTransfer.files;
            updateFileList(e.dataTransfer.files);
        });

        function updateFileList(files) {
            if (files.length === 0) {
                fileList.innerHTML = '';
                return;
            }
            const fileNames = Array.from(files).map(file => file.name).join(', ');
            fileList.innerHTML = `<span class="font-medium">${files.length} archivo(s) seleccionado(s):</span> ${fileNames}`;
        }
    }

    document.addEventListener('DOMContentLoaded', function () {
        const dropZones = document.querySelectorAll('[data-file-upload]');
        dropZones.forEach(dropZone => {
            inicializarFileUpload(dropZone);
        });
    });
})();
