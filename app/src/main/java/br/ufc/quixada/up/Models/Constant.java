package br.ufc.quixada.up.Models;

/**
 * Created by Macelo on 01/12/2017.
 */

public class Constant {

//    Status das negociações
    public static final int CLOSED_NEGOTIATION = 0;
    public static final int OPENED_NEGOTIATION = 1;
    public static final int CANCELLED_NEGOTIATION = 2;

//    Tipos de negociações
    public static final int NEGOTIATION_TYPE_BUY = 0;
    public static final int NEGOTIATION_TYPE_SELL = 1;

//    Callers da activity chat
    public static final int CHAT_CALLER_POST_ADAPTER = 0;
    public static final int CHAT_CALLER_NEGOTIATION_ADAPTER = 1;

//    Callers da activity anúncio
    public static final int POST_CALLER_MAIN_ACTIVITY = 0;
    public static final int POST_CALLER_CHAT_ACTIVITY = 1;

//    Controle de exibição das negociações
    public static final int SHOW_SELL_NEGOTIATIONS = 0;
    public static final int SHOW_BUY_NEGOTIATIONS = 1;
    public static final int HIDE_SELL_NEGOTIATIONS = 2;
    public static final int HIDE_BUY_NEGOTIATIONS = 3;

//    Tipos de mensagens
    public static final int SENT_MESSAGE = 0;
    public static final int SENT_CONTIGUOUS_MESSAGE = 1;
    public static final int RECEIVED_MESSAGE = 2;
    public static final int RECEIVED_CONTIGUOUS_MESSAGE = 3;

}
