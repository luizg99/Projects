import pygame
import sys
import random

# Inicialização do Pygame
pygame.init()

# Definir cores
BLACK = (0, 0, 0)
WHITE = (255, 255, 255)
GREEN = (0, 255, 0)

# Configurações da tela
WIDTH, HEIGHT = 640, 480
GRID_SIZE = 20
GRID_WIDTH = WIDTH // GRID_SIZE
GRID_HEIGHT = HEIGHT // GRID_SIZE

# Inicialização da tela
screen = pygame.display.set_mode((WIDTH, HEIGHT))
pygame.display.set_caption("Jogo da Cobrinha")

# Função para desenhar a cobrinha
def draw_snake(snake):
    for segment in snake:
        pygame.draw.rect(screen, GREEN, (segment[0] * GRID_SIZE, segment[1] * GRID_SIZE, GRID_SIZE, GRID_SIZE))

# Função principal do jogo
def main():
    clock = pygame.time.Clock()
    snake = [(5, 5)]
    food = (random.randint(0, GRID_WIDTH - 1), random.randint(0, GRID_HEIGHT - 1))
    direction = (1, 0)

    while True:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                pygame.quit()
                sys.exit()
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_UP and direction != (0, 1):
                    direction = (0, -1)
                elif event.key == pygame.K_DOWN and direction != (0, -1):
                    direction = (0, 1)
                elif event.key == pygame.K_LEFT and direction != (1, 0):
                    direction = (-1, 0)
                elif event.key == pygame.K_RIGHT and direction != (-1, 0):
                    direction = (1, 0)

        # Atualizar a posição da cabeça da cobrinha
        new_head = (snake[0][0] + direction[0], snake[0][1] + direction[1])

        # Verificar colisão com a parede
        if new_head[0] < 0 or new_head[0] >= GRID_WIDTH or new_head[1] < 0 or new_head[1] >= GRID_HEIGHT:
            pygame.quit()
            sys.exit()

        # Verificar colisão com a própria cobrinha
        if new_head in snake:
            pygame.quit()
            sys.exit()

        # Verificar se a cobrinha comeu a comida
        if new_head == food:
            snake.insert(0, food)
            food = (random.randint(0, GRID_WIDTH - 1), random.randint(0, GRID_HEIGHT - 1))
        else:
            snake.insert(0, new_head)
            snake.pop()

        # Preencher a tela com a cor de fundo
        screen.fill(BLACK)

        # Desenhar a comida
        pygame.draw.rect(screen, WHITE, (food[0] * GRID_SIZE, food[1] * GRID_SIZE, GRID_SIZE, GRID_SIZE))

        # Desenhar a cobrinha
        draw_snake(snake)

        pygame.display.flip()
        clock.tick(10)

if __name__ == "__main__":
    main()