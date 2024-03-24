package server;

import java.net.Socket;

import server.model.Session;
import utils.ConsoleColors;
import utils.IoTCLI;
import utils.IoTMessage;
import utils.IoTMessageType;
import utils.IoTOpcodes;
import utils.IoTStream;

public class IoTServerThread extends Thread {
    private IoTServerRequestHandler handler;
    private IoTServerDatabase dbContext;
    private Session session;

    private Boolean running = false;
    private IoTStream ioTStream;
    private IoTCLI cli;
    private String color;

    public IoTServerThread(Socket socket) {
        this.ioTStream = new IoTStream(socket);
        this.running = true;
        this.cli = IoTCLI.getInstance();
        this.color = ConsoleColors.Service.getInstance().getRandomUnusedColor("Regular");
        this.handler = IoTServerRequestHandler.getInstance();
        this.dbContext = IoTServerDatabase.getInstance();
        this.session = new Session();   

    }

    /**
     * Waits for client requests and forwards them the handler.
     */
    @Override
    public void run() {
        while (this.running && this.ioTStream.ready() && !isInterrupted())
            mainLoop();
    }

    @Override
    public void interrupt() {
        IoTMessageType message = new IoTMessage();
        message.setOpCode(IoTOpcodes.EOS);

        processAndReply(message);
        IoTMessageType response = (IoTMessageType) this.ioTStream.read();
        if (response != null && IoTOpcodes.OK_ACCEPTED.equals(response.getOpcode()))
            cli.printInfo(String.format(
                "Session %s terminated gracefully!", session.toString()
            ));
        super.interrupt();
    }

    private void processAndReply(IoTMessageType message) {
        IoTMessageType responseMessage = handler.process(message, session, dbContext);
        cli.print(
            String.format("Processed message and response will be %s", responseMessage), session.toString(), color
        );

        if (responseMessage != null) {
            cli.print("Sending!", session.toString(), color);
            if (this.ioTStream.write(responseMessage))
                cli.print("Sent!", session.toString(), color);
        }
    }

    private void mainLoop() {
        IoTMessageType receivedMessage = (IoTMessageType) this.ioTStream.read();
        if (receivedMessage == null)
            return;
        cli.print(
            String.format("Received message %s!", receivedMessage), session.toString(), color
        );
        // response
        processAndReply(receivedMessage);

    }
}