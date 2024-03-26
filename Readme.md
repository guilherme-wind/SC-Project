## Projeto 1 fase 1 Segurança e Confiabilidade 2023-24

### Grupo 24

### Visão geral
A primeira fase do projeto 1 tem como o objetivo a implementação do programa do modelo cliente-servidor que simulam dispositivos sensoriais inteligentes e o central de dados. Além do envio dos dados(medições de temperatura e imagens), os dispositivos podem ser registados em nome de um utilizador. Assim, o trabalho consiste em gestão de relações entre utilizadores, dispositivos e domínios.

### Autores
- Guilherme Marcelo [@fc58173](fc58173@alunos.fc.ul.pt)
- Guilherme Wind [@fc58640](fc58640@alunos.fc.ul.pt)
- Xiting Wang [@fc58183](fc58183@alunos.fc.ul.pt)

### Compilação
- ##### Cliente - `IoTDevice`
    Para compilar o código fonte do cliente, abra um terminal no diretório raíz do projeto, i.e., no diretório anterior de `bin` e de `src`, e execute o seguinte comando `java -sourcepath ./src/ -d ./bin/ ./src/client/IoTDevice.java`. Os ficheiros `.class` gerados ficarão no diretório `bin`.

    Depois de compilar para `.class`, para criar o ficheiro `IoTDevice.jar`, mantenha o terminal no diretório raíz do projeto e execute `jar -cfe IoTDevice.jar bin.client.IoTDevice bin/*`, o `.jar` criado ficará no mesmo diretório do terminal.

- ##### Servidor - `IoTServer`
    Para compilar o código fonte do servidor, abra um terminal no diretório raíz do projeto, i.e., no diretório anterior de `bin` e de `src`, e execute o seguinte comando `java -sourcepath ./src/ -d ./bin/ ./src/server/IoTServer.java`. Os ficheiros `.class` gerados ficarão no diretório `bin`.

    Depois de compilar para `.class`, para criar o ficheiro `IoTServer.jar`, mantenha o terminal no diretório raíz do projeto e execute `jar -cfe IoTServer.jar bin.server.IoTServer bin/*`, o `.jar` criado ficará no mesmo diretório do terminal.

### Execução
É obrigatório ter o ficheiro `program-info.program` no diretório `server_files/metadata/` com o nome do ficheiro `.jar` e o seu tamanho, caso contrário o cliente não conseguirá autenticar o seu programa.

É necessário lançar primeiro o servidor, se não o cliente não conseguirá estabelecer a ligação.
- #### Executar `.jar`

  - ##### Cliente - `IoTDevice.jar`

    Para executar o programa cliente, abra um terminal no diretório onde se encontra o ficheiro `IoTDevice.jar` e execute um dos seguintes comandos:
    ```bash
    java -jar IoTDevice.jar <IP servidor>:<Porto servidor> <ID dispositivo> <Nome utilizador>
    ```
    ```bash
    java -jar IoTDevice.jar <IP servidor> <ID dispositivo> <Nome utilizador>
    ```
    Se o porto do servidor não for fornecida, o programa usará o porto de omissão, 12345, para estabelecer ligação ao servidor.

  - ##### Servidor - `IoTServer.jar`
    Para executar o servidor, abra um terminal no diretório onde se encontra o ficheiro `IoTDevice.jar` e execute um dos seguintes comandos:
    ```bash
    java -jar IoTServer.jar 
    ```
    ```bash
    java -jar IoTServer.jar <Porto servidor>
    ```

    Em geral, é sugerido executar o servidor sem indicar o porto, porém em casos necessários, pode fornecer o porto.

### Limitações da implementação
A implementação do projeto cumpre os requisitos do [enunciado](https://moodle.ciencias.ulisboa.pt/mod/resource/view.php?id=223109). No entanto, o servidor não é capaz de terminar os clientes quando encerra.

### Ficheiros entregues
Na pasta de entrega, deve conter um diretório `src` com os ficheiros de código fonte; um diretório `bin` vazio para guardar os ficheiros `.class` gerados na compilação e uma pasta `server_files` que contém uma pasta `metadata` que tem um ficheiro de texto `program-info.program`, este tem a informação, nome e o tamanho, do `IoTDevice.jar` entregue, se for gerado um novo programa do cliente, o conteúdo deste ficheiro também é necessário ser atualizado.

### Informação extra
Para verificar a integridade dos ficheiros, estão em baixo o checksum dos programas executáveis:

- `IoTDevice.jar`
  - MD5 ```df057f85d516f646a03c9e6530a0cae2```
  - SHA256 ```760aee14922caa1e4817b0d9ff7c90bc106b8d5b9ae3dd257c037acada6e84df```

- `IoTServer.jar`
  - MD5 ```621c23d236bec0df7a27ba275327d28f```
  - SHA256 ```262e4093a0ddb06fae7280ca7402e4bb5ca25e908f389526dfc52cb6e077057c```