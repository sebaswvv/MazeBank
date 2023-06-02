interface RegisterRequest {
  email: string;
  bsn: number;
  firstName: string;
  lastName: string;
  password: string;
  phoneNumber: string;
  dateOfBirth?: string;
}

export default RegisterRequest;
