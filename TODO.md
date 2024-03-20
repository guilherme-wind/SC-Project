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
- IoTDevice.java: classe principal do cliente 
    - [Opcional] UI.java: CLI 
    - ClientStub/ClientNetwork.java: comunicacao com o servidor, pedido e respostas. 
    - FileHandler.java: gestao de ficheiros
- IoTMessage.java: classe que define a mensagem entre cliente e servidor. Pode ser depois substituido por protobuf 
- IoTServer.java: classe principal do servidor
    - ServerNetwork.java: aceitacao ligacoes de clientes e lanca threads
    - ClientHandler.java: aceita pedidos de clientes e comunica 
    - UsersManager.java: gere os utilizadores e os grupos, faz autenticacao e verificacao de permissoes antes de executar pedidos. Obs.: pode haver um outro modulo apenas para a autenticacao inicial, i.e., verificar <user-id, password>, <device-id>, <program-name> e <program-size>
    - DataSaver.java: armazenamento persistente, corresponde a camada de base de dados

### Utilizacao de IoTMessage
| operacao | opcode pedido | campos usados no pedido | opcode resposta |
| ----- | ----- | ----- | ----- |
| autenticacao utilizador | VALIDATE_USER | userid, userpwd | WRONG_PWD, OK_NEW_USER, OK_USER |
| autenticacao dispositivo | VALIDATE_DEVICE | devid | NOK_DEVID, OK_DEVID |
| autenticacao programa | VALIDATE_PROGRAM | program_name, program_size | NOK_TESTED, OK_TESTED |
| criacao dominio | CREATE_DOMAIN | domain_name | NOK_ALREADY_EXISTS, OK_ACCEPTED |
| adicionar utilizador ao dominio | ADD_USER_DOMAIN | userid, domain_name | NOK_NO_USER, NOK_NO_DOMAIN, NOK_NO_PERMISSIONS, NOK_ALREADY_EXISTS, OK_ACCEPTED |
| registar dispositivo atual no dominio | REGISTER_DEVICE_DOMAIN | devid, domain_name | NOK_NO_DOMAIN, NOK_NO_PERMISSIONS, NOK_ALREADY_EXISTS, OK_ACCEPTED |
| enviar valor | SEND_TEMP | temp | OK_ACCEPTED |
| enviar imagem | SEND_IMAGE | img | OK_ACCEPTED |
| receber temperatura | GET_TEMP | domain_name | NOK_NO_PERMISSIONS, NOK_NO_DOMAIN, OK_ACCEPTED |
| receber imagem | GET_DEVICE_IMAGE | userid, devid | NOK_NO_PERMISSIONS, NOK_NO_USER, NOK_NO_DATE, OK_ACCEPTED |
### Utilizacao dos Modelos (Server side)
let executor := User(user, pass) # user of the current session
let device := Device(name, executorName) # device of the current session
let domains := HashMap<String, Domain>();
let users := HashMap<String, User>();
__

CREATE(<dm>: str):

    if domains.contains(dm):
        return NOK

    domain = Domain(dm, executor)
    domains.put(dm, domain)
    return OK
__

ADD(<user1>: str <dm>: str):

    if !domains.contains(dm):
        return NODM

    if !users.contains(user1):
        return NOUSER

    (Domain) domain = domains.get(dm)
    if !domain.ownedBy(executor):
        return NOPERM

    // do we need to check if the user is already added?
    (User) user = users.get(user1)
    domain.addUser(user);
    return OK

___

RD(<dm>: str):

    if !domains.contains(dm):
        return NODM

    (Domain) domain = domains.get(dm)
    if !domain.contains(as):
        return NOPERM
    
    domain.registerDevice(device);
    return OK

__

RT (<dm>: str):

    if !domains.contains(dm):
        return NODM

    (Domain) domain = domains.get(dm)
    if !domain.ownedBy(executor):
        return NOPERM

### Codigos por corrigir
- `IoTServerRequestHandler.handleReceiveImage()`
- `Device.writeImage()` e `Device.writeTemperature()`, ...
- `IoTServerDataBase.canUserReceiveDataFromDevice()` deve permitir o utilizador receber imagem sem ter o seu
  dispositivo num dominio
- Separar responsabilidades de Database e Persistance