# Capital Gain Calculator

Este projeto calcula o imposto sobre ganhos de capital a partir de uma lista de operações de compra e venda de ativos financeiros. O cálculo é realizado com base em uma estratégia de imposto específica.

## Pré-requisitos

- **Java 17** ou superior
- **Maven** (opcional, se você quiser compilar o projeto)
- Um arquivo JSON com os dados das operações

## Estrutura do Projeto

- **src**: Código-fonte do projeto
- **output**: Diretório onde os resultados do cálculo serão salvos
- **pom.xml**: Arquivo de configuração do Maven (se aplicável)

## Compilação

Se você estiver usando Maven, pode compilar o projeto executando o seguinte comando no diretório raiz do projeto:

```bash
mvn clean package
```

Isso gerará um arquivo JAR na pasta `target`.

## Executando o Projeto

1. **Prepare seu arquivo JSON** com as operações de compra e venda de ativos. O arquivo deve estar no seguinte formato:

    ```json
    [
    {
        "operation": "buy",
        "unit-cost": 10.00,
        "quantity": 100
    },
    {
        "operation": "sell",
        "unit-cost": 15.00,
        "quantity": 50
    },
    {
        "operation": "sell",
        "unit-cost": 15.00,
        "quantity": 50
    }
    ]
    ```

2. **Execute o JAR**, passando o caminho absoluto do seu arquivo JSON como argumento. Use o seguinte comando:

    ```bash
    java -jar target/capital-gains-1.0-SNAPSHOT.jar /caminho/para/seu/arquivo.json
    ```

3. **Verifique os resultados**. Os resultados do cálculo do imposto serão salvos no diretório `output` como um arquivo JSON. Além disso eles são printados durante a execução do script.
