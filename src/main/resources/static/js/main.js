document.addEventListener("DOMContentLoaded", function () {
    const slideContainer = document.querySelector('.carousel-slide');
    if (!slideContainer) {
        return; // Sai se não houver carrossel na página
    }

    const items = slideContainer.querySelectorAll('.carousel-item');
    const prevButton = document.querySelector('.carousel-control.prev');
    const nextButton = document.querySelector('.carousel-control.next');

    if (items.length <= 1) {
        // Esconde botões e para o script se houver 1 ou 0 slides
        if (prevButton) prevButton.style.display = 'none';
        if (nextButton) nextButton.style.display = 'none';
        return;
    }

    const totalItems = items.length;
    let currentIndex = 0;
    let slideInterval;

    function updateCarousel() {
        const offset = -currentIndex * 100;
        slideContainer.style.transform = `translateX(${offset}%)`;
    }

    function showSlide(index) {
        currentIndex = (index + totalItems) % totalItems;
        updateCarousel();
    }

    // Expõe a função globalmente para os botões HTML
    window.moveSlide = function (direction) {
        showSlide(currentIndex + direction);
        // Reinicia o intervalo para não mudar imediatamente após um clique manual
        clearInterval(slideInterval);
        startSlideShow();
    };

    function startSlideShow() {
        clearInterval(slideInterval); // Garante que não haja múltiplos intervalos
        slideInterval = setInterval(() => {
            showSlide(currentIndex + 1);
        }, 5000); // Muda a cada 5 segundos
    }

    const carouselContainerElement = document.querySelector('.carousel-container');
    if (carouselContainerElement) {
        carouselContainerElement.addEventListener('mouseenter', () => {
            clearInterval(slideInterval);
        });

        carouselContainerElement.addEventListener('mouseleave', () => {
            startSlideShow();
        });
    }

    // Garante que o primeiro slide seja exibido corretamente ao carregar
    updateCarousel();
    startSlideShow();
});