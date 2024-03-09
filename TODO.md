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

### Utilizacao de IoTMessage
| operacao | opcode | campos usados no pedido |
| ----- | ----- | ----- |
| autenticacao utilizador | VALIDATE_USER | userid, userpwd |
| autenticacao dispositivo | VALIDATE_DEVICE | devid |
| autenticacao programa | VALIDATE_PROGRAM | program_name, program_size |
| criacao dominio | CREATE_DOMAIN | domain_name |
| adicionar utilizador ao dominio | ADD_USER_DOMAIN | userid, domain_name |
| registar dispositivo atual no dominio | REGISTER_DEVICE_DOMAIN | devid, domain_name |
| enviar valor | SEND_TEMP | temp |
| enviar imagem | SEND_IMAGE | stand by |

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
