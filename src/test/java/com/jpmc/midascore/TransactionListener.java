@Component
public class TransactionListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository txRepository;

    public TransactionListener(UserRepository userRepository,
                               TransactionRecordRepository txRepository) {
        this.userRepository = userRepository;
        this.txRepository = txRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}")
    @Transactional
    public void listen(Transaction tx) {

        var senderOpt = userRepository.findById(tx.senderId());
        var recipientOpt = userRepository.findById(tx.recipientId());

        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            return;
        }

        User sender = senderOpt.get();
        User recipient = recipientOpt.get();

        if (sender.getBalance() < tx.amount()) {
            return;
        }

        // update balances
        sender.setBalance(sender.getBalance() - tx.amount());
        recipient.setBalance(recipient.getBalance() + tx.amount());

        // save everything
        txRepository.save(new TransactionRecord(sender, recipient, tx.amount()));
        userRepository.save(sender);
        userRepository.save(recipient);
    }
}
