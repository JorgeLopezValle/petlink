document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("input[type='number']").forEach(input => {
        if (input.dataset.spinnerProcessed) return;
        input.dataset.spinnerProcessed = "true";

        const container = document.createElement("div");
        container.className = "spinner-buttons-container";

        const upButton = document.createElement("button");
        upButton.type = "button";
        upButton.className = "spinner-button spinner-button-up";
        upButton.setAttribute("tabindex", "-1");
        upButton.setAttribute("aria-label", `Aumentar ${input.name || "valor"}`);
        upButton.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" d="M5 15l7-7 7 7" />
            </svg>
        `;

        const downButton = document.createElement("button");
        downButton.type = "button";
        downButton.className = "spinner-button spinner-button-down";
        downButton.setAttribute("tabindex", "-1");
        downButton.setAttribute("aria-label", `Disminuir ${input.name || "valor"}`);
        downButton.innerHTML = `
            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" d="M19 9l-7 7-7-7" />
            </svg>
        `;

        container.appendChild(upButton);
        container.appendChild(downButton);

        const parent = input.parentElement;
        if (parent) {
            if (!parent.classList.contains("relative")) {
                parent.classList.add("relative");
            }
            parent.appendChild(container);
        }

        upButton.addEventListener("click", (e) => {
            e.preventDefault();
            input.stepUp();
            input.dispatchEvent(new Event("input", { bubbles: true }));
            input.dispatchEvent(new Event("change", { bubbles: true }));
        });

        downButton.addEventListener("click", (e) => {
            e.preventDefault();
            input.stepDown();
            input.dispatchEvent(new Event("input", { bubbles: true }));
            input.dispatchEvent(new Event("change", { bubbles: true }));
        });

        const updateButtonStates = () => {
            const value = parseFloat(input.value) || 0;
            const min = input.min ? parseFloat(input.min) : -Infinity;
            const max = input.max ? parseFloat(input.max) : Infinity;

            upButton.disabled = value >= max;
            downButton.disabled = value <= min;
        };

        input.addEventListener("input", updateButtonStates);
        input.addEventListener("change", updateButtonStates);

        updateButtonStates();
    });
});