## Tarefas para fazer (todos podem atualizar)

Este documento serve para a divisao e gestao de tarefas do projeto e planificacao.
O projeto e primeiramente dividido em objetivos/tarefas grandes e estes especificam ao longo do tempo.
Caso alguem queira tomar a posse do desenvolvimento de tarefas, simplesmente escreve o nome a frente da tarefa, ex.: - Implementar CLI (Wang)

### IoTDevice (cliente)
- Enviar imagens e valores
- Ler imagens e valores de outros dispositivos
- CLI simples

### IoTServer (servidor multithreaded)
- Manter informacoes sobre utilizadores, dominios e dispositivos(instancias de IoTDevice)
- Autenticacao de utilizadores
- Colecionar e partilhar dados dos dispositivos

### Planificacao
- IoTDevice.java: classe principal do cliente (Wang)
    - [Opcional] UI.java: CLI (Wang)
    - ClientStub/ClientNetwork.java: comunicacao com o servidor, pedido e respostas. (Wang)
    - FileHandler.java: gestao de ficheiros
- IoTMessage.java: classe que define a mensagem entre cliente e servidor. Pode ser depois substituido por protobuf (Wind)
- IoTServer.java: classe principal do servidor
    - ServerNetwork.java: aceitacao ligacoes de clientes e lanca threads
    - ClientHandler.java: aceita pedidos de clientes e comunica 
    - UsersManager.java: gere os utilizadores e os grupos, faz autenticacao e verificacao de permissoes antes de executar pedidos. Obs.: pode haver um outro modulo apenas para a autenticacao inicial, i.e., verificar <user-id, password>, <device-id>, <program-name> e <program-size>
    - DataSaver.java: armazenamento persistente, corresponde a camada de base de dados