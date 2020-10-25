#irc-chat-server-test

Implements a simple text based chat server based on Netty framework and the hexagonal architecture

Commands set for this server:

**/login** name password — if user not exists creates profile else login

**/join** channel — Try to join chat room (i.e. channel) (max 10 active clients per channel is needed) If client’s limit exceeded - send error, otherwise join channel and send last 100 messages of activity

**/leave** — Logs out the user connection, but not terminate the client connection with server

**/quit** chatRoomName? — If chatRoomName (i.e. channelName) is not defined, user leaves active channel and joins automatically another chat room (i.e. channel)

**/users** — Show users in the active channel

**/exit** — Terminates the client connection with server

text message terminated with CR - sends message to current channel. Server must send new message to all connected to this channel clients. We should be able to check this server via simple text based telnet command.