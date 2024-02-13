package moe.seikimo.magixbot.utils;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.xigam.cch.command.Command;
import tech.xigam.cch.command.SubCommand;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;

public interface ReflectionUtils {
    Logger logger = LoggerFactory.getLogger("Reflector");
    Reflections reflector = new Reflections("moe.seikimo.magixbot");

    /**
     * @return A collection of instances of all commands.
     */
    static Collection<Command> getAllCommands() {
        var instances = new ArrayList<Command>();
        for (var commandClass : reflector.getSubTypesOf(Command.class)) try {
            if (commandClass.isMemberClass() ||
                    Modifier.isAbstract(commandClass.getModifiers()) ||
                    SubCommand.class.isAssignableFrom(commandClass)) continue;
            instances.add(commandClass.getDeclaredConstructor().newInstance());
        } catch (Exception exception) {
            logger.warn("Unable to create an instance of " + commandClass.getName(), exception);
        }

        return instances;
    }
}
