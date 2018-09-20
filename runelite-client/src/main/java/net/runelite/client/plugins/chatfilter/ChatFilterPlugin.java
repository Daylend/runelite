package net.runelite.client.plugins.chatfilter;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ConfigChanged;
import net.runelite.api.events.SetMessage;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.ChatboxInputListener;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ChatboxInput;
import net.runelite.client.events.PrivateMessageInput;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.regex.Matcher;

@PluginDescriptor(
    name = "Chat Filter",
    description = "Removes chat messages based on blacklisted words",
    tags = {"chat","blacklist","filter"}
)
@Slf4j
public class ChatFilterPlugin extends Plugin implements ChatboxInputListener
{
    private static final Splitter COMMA_SPLITTER = Splitter
        .on(",")
        .omitEmptyStrings()
        .trimResults();

    private static final Joiner COMMA_JOINER = Joiner.on(",").skipNulls();

    private List<String> blockedStringList = new CopyOnWriteArrayList<>();

    @Inject
    private Client client;

    @Inject
    private ChatFilterConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private ScheduledExecutorService executor;

    @Inject
    private ItemManager itemManager;

    @Inject
    private ChatMessageManager chatMessageManager;

    @Provides
    ChatFilterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ChatFilterConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("chatfilter"))
        {
            reset();
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
//        if (chatMessage.getType() == ChatMessageType.PUBLIC)
//        {
//            String message = chatMessage.getMessage();
//            log.debug(message + "REEEEEE2");
//        }
    }

    /**
     * Checks if the chat message is a command.
     *
     * @param setMessage The chat message.
     */
    @Subscribe
    public void onSetMessage(SetMessage setMessage)
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        switch (setMessage.getType())
        {
            case PUBLIC:
            case PUBLIC_MOD:
            case CLANCHAT:
            case PRIVATE_MESSAGE_RECEIVED:
            case PRIVATE_MESSAGE_SENT:
                break;
            default:
                return;
        }

        String message = setMessage.getValue();
        MessageNode messageNode = setMessage.getMessageNode();

        // clear RuneLite formatted message as the message node is
        // being reused
        messageNode.setRuneLiteFormatMessage(null);
        executor.submit(() -> blockMessage(setMessage.getType(), setMessage, blockedStringList));

    }

    @Override
    protected void startUp()
    {
        reset();
    }

    @Override
    protected void shutDown() throws Exception
    {
        blockedStringList = null;
    }

    @Override
    public boolean onChatboxInput(ChatboxInput chatboxInput)
    {
        return true;
    }

    @Override
    public boolean onPrivateMessageInput(PrivateMessageInput privateMessageInput)
    {
        return true;
    }

    private void blockMessage(ChatMessageType type, SetMessage setMessage, List<String> blockList)
    {
        final String playerName = client.getLocalPlayer().getName();
        String message = setMessage.getValue();
        // TODO: Check if message is from player
        if (compareStringToList(message, blockList))
        {
            String response = new ChatMessageBuilder()
                .append(ChatColorType.HIGHLIGHT)
                .append("blocked")
                .build();

            final MessageNode messageNode = setMessage.getMessageNode();
            messageNode.setSender("Message");
            messageNode.setName("Message");
            messageNode.setRuneLiteFormatMessage(response);
            chatMessageManager.update(messageNode);
            client.refreshChat();
        }
    }

    private static boolean compareStringToList(String str, List<String> strList)
    {
        return strList.parallelStream().anyMatch(str.toLowerCase()::contains);
    }

    private void reset()
    {
        blockedStringList = COMMA_SPLITTER.splitToList(config.getBlacklistedStrings().toLowerCase());
    }
}
