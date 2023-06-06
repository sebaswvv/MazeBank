import { AccountType } from "../../enums/AccountType";

interface AccountRequest {
    accountType: AccountType;
    userId: number;
    active: boolean;
    absoluteLimit: number;
}

export default AccountRequest;