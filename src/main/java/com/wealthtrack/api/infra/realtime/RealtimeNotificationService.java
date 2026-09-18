package com.wealthtrack.api.infra.realtime;

import io.ably.lib.rest.AblyRest;
import io.ably.lib.rest.Channel;
import io.ably.lib.types.AblyException;
import org.springframework.stereotype.Service;

@Service
public class RealtimeNotificationService {

    private final AblyRest ablyRest;

    public RealtimeNotificationService(AblyRest ablyRest) {
        this.ablyRest = ablyRest;
    }

    public void notifyBalanceUpdate(String accountId, String newBalance) {
        try {
            // 1. Cria ou pega o canal específico dessa conta (Ex: "account-f47ac10b...")
            String channelName = "account-" + accountId;
            Channel channel = ablyRest.channels.get(channelName);

            // 2. Publica uma mensagem no canal.
            // O evento se chama "BALANCE_UPDATED". O payload é o novo saldo.
            channel.publish("BALANCE_UPDATED", newBalance);

            System.out.println("Aviso em tempo real enviado via Ably para a conta: " + accountId);

        } catch (AblyException e) {
            // Não queremos que o sistema quebre se o Ably estiver fora do ar
            System.err.println("Erro ao enviar notificação real-time via Ably: " + e.getMessage());
        }
    }
}