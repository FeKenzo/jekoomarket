document.addEventListener("DOMContentLoaded", function () {
    // Seleciona os elementos do carrossel
    const slideContainer = document.querySelector('.carousel-slide');
    if (!slideContainer) {
        // Se não houver carrossel na página, interrompe a execução do script
        return;
    }

    const items = document.querySelectorAll('.carousel-item');
    if (items.length <= 1) {
        // Se houver 1 ou 0 slides, não faz nada
        return;
    }

    const totalItems = items.length;
    let currentIndex = 0;
    let slideInterval;

    /**
     * Atualiza a posição do slide com base no índice atual.
     */
    function updateCarousel() {
        const offset = -currentIndex * 100; // Deslocamento em porcentagem
        slideContainer.style.transform = `translateX(${offset}%)`;
    }

    /**
     * Move para um slide específico.
     * @param {number} index - O índice do slide para o qual mover.
     */
    function showSlide(index) {
        // Garante que o índice esteja dentro dos limites (0 a totalItems-1)
        currentIndex = (index + totalItems) % totalItems;
        updateCarousel();
    }

    /**
     * Move para o slide seguinte ou anterior.
     * Esta função é exposta globalmente para ser chamada pelos botões no HTML.
     * @param {number} direction - A direção para mover (-1 para anterior, 1 para próximo).
     */
    window.moveSlide = function (direction) {
        showSlide(currentIndex + direction);
    };

    /**
     * Inicia a transição automática de slides.
     */
    function startSlideShow() {
        // Limpa qualquer intervalo anterior para evitar múltiplos timers
        clearInterval(slideInterval);
        slideInterval = setInterval(() => {
            showSlide(currentIndex + 1);
        }, 5000); // Muda a cada 5 segundos
    }

    // Para a transição quando o mouse está sobre o carrossel
    const carouselContainer = document.querySelector('.carousel-container');
    carouselContainer.addEventListener('mouseenter', () => {
        clearInterval(slideInterval);
    });

    // Recomeça a transição quando o mouse sai
    carouselContainer.addEventListener('mouseleave', () => {
        startSlideShow();
    });

    // Inicia o show de slides assim que a página carrega
    startSlideShow();
});