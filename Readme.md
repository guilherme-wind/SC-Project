## Projeto 1 fase 1 Segurança e Confiabilidade 2023-24

### Grupo 24

### Visão geral
A primeira fase do projeto 1 tem como o objetivo a implementação do programa do modelo cliente-servidor que simulam dispositivos sensoriais inteligentes e o central de dados. Além do envio dos dados(medições de temperatura e imagens), os dispositivos podem ser registados em nome de um utilizador. Assim, o trabalho consiste em gestão de relações entre utilizadores, dispositivos e domínios.

### Autores
- Guilherme Marcelo [@fc58173](fc58173@alunos.fc.ul.pt)
- Guilherme Wind [@fc58640](fc58640@alunos.fc.ul.pt)
- Xiting Wang [@fc58183](fc58183@alunos.fc.ul.pt)

### Compilação
- Cliente - `IoTDevice`
    Para compilar o cliente, TODO

- Servidor - `IoTServer`
    TODO

### Execução
A execução do programa requer a JVM e pode ser realizada com ficheiros `.jar` e com `.class`.

- #### Executar `.jar`

  - Cliente - `IoTDevice.jar`
    Para executar o programa cliente, abra um terminal no diretório onde se encontra o ficheiro `IoTDevice.jar` e execute um dos seguintes comandos:
    ```bash
    java -jar IoTDevice.jar <IP servidor>:<Porto servidor> <ID dispositivo> <Nome utilizador>
    ```
    ```bash
    java -jar IoTDevice.jar <IP servidor> <ID dispositivo> <Nome utilizador>
    ```
    Se o porto do servidor não for fornecida, o programa usará o porto de omissão, 12345, para estabelecer ligação ao servidor.

  - Servidor - `IoTServer.jar`
    Para executar o servidor, abra um terminal no diretório onde se encontra o ficheiro `IoTDevice.jar` e execute um dos seguintes comandos:
    ```bash
    java -jar IoTServer.jar 
    ```
    ```bash
    java -jar IoTServer.jar <Porto servidor>
    ```

    Em geral, é sugerido executar o servidor sem indicar o porto, porém em casos necessários, pode fornecer o porto.

### Limitações da implementação
A implementação do projeto cumpre os requisitos do [enunciado](https://moodle.ciencias.ulisboa.pt/mod/resource/view.php?id=223109).