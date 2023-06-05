interface UserPatchRequest {
  email?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  dayLimit?: number;
  transactionLimit?: number;
}

export default UserPatchRequest;
