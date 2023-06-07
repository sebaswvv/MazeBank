export default interface TransactionRequest {
    amount: number;
    description: string;
    senderIban: string;
    receiverIban: string;
}