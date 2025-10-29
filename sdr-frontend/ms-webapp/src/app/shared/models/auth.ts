export interface LoginRequest { 
  email: string; 
  password: string; 
}

export interface RegisterRequest {
  pseudo: string;
  email: string;
  steamId: string;
  password: string;
  confirmPassword: string;
  discordId?: string | null;
}

export interface AuthResponse {
  token: string;
  steamPseudo?: string;
  steamAvatar?: string;
  gamesHours?: Record<string, number>;
}
