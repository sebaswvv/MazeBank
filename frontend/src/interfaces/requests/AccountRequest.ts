import { AccountType } from "../../enums/AccountType";

interface AccountRequest {
    accountType: AccountType;
    userId: number;
    isActive: boolean;
    absoluteLimit: number;
}

export default AccountRequest;