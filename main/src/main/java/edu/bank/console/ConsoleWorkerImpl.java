package edu.bank.console;

import edu.bank.enm.Command;
import edu.bank.exeption.UnexpectedInternalError;
import edu.bank.service.AccountService;
import edu.bank.service.BankService;
import edu.bank.service.UserService;
import edu.bank.service.impl.AccountServiceImpl;
import edu.bank.service.impl.BankServiceImpl;
import edu.bank.service.impl.UserServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConsoleWorkerImpl implements ConsoleWorker {

    private final BankService bankService = new BankServiceImpl();
    private final UserService userService = new UserServiceImpl();
    private final AccountService accountService = new AccountServiceImpl();
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    @Override
    public void simulateBankOperations() {
        System.out.println("Welcome!\nTo view a list of all available commands, type \"help\"." +
                "\nTo exit the application, use \"exit\".");
        while (true) {
            try {
                String command = reader.readLine();
                if (command.equals("exit")) break;
                parseAndExecuteCommand(command);
            } catch (IOException e) {
                System.out.println("Invalid command format. To view all commands use \"help\"");
            } catch (UnexpectedInternalError e) {
                System.out.println("Sorry, an internal error has occurred. Try again later");
            }
        }
    }

    private void parseAndExecuteCommand(String fullCommand) throws IOException {
        String[] commandParts = fullCommand.split(" ");
        Command command = getCommandByName(commandParts[0]);
        Map<String, String> params = new HashMap<>();
        for (int i = 1; i < commandParts.length; i++) {
            String paramAndValue = commandParts[i];
            if (!paramAndValue.startsWith("-") || !paramAndValue.contains("=") || paramAndValue.endsWith("="))
                throw new IOException();
            String[] splittedParamAndValue = paramAndValue.split("=");
            if (splittedParamAndValue.length != 2) throw new IOException();
            String param = splittedParamAndValue[0].substring(1);
            String value = splittedParamAndValue[1];
            if (!isCommandParamsValid(command, param)) throw new IOException();
            if (!params.containsKey(param)) params.put(param, value);
            else throw new IOException();
        }
        executeCommand(command, params);
    }

    private void executeCommand(Command command, Map<String, String> params) throws IOException, NumberFormatException {
        switch (command) {
            case CREATE_BANK -> bankService.createBank(params);
            case UPDATE_BANK -> bankService.updateBank(params);
            case CREATE_INDIVIDUAL -> userService.createIndividual(params);
            case CREATE_LEGAL_ENTITY -> userService.createLegalEntity(params);
            case GET_USER_ACCOUNTS -> accountService.getAllByUser(params);
            case TRANSFER_MONEY -> accountService.transferMoney(params);
        }
    }


    private Command getCommandByName(String commandName) throws IOException {
        return Arrays.stream(Command.values())
                .filter(c -> c.getCommandName().equals(commandName))
                .findFirst()
                .orElseThrow(IOException::new);
    }

    private boolean isCommandParamsValid(Command command, String commandParamName) {
        return Arrays.stream(command.getCommandParams())
                .anyMatch(p -> p.getParamName().equals(commandParamName));
    }
}