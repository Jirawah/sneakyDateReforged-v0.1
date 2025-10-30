export interface UserSummary {
  id: number | string;
  pseudo: string;
  avatarUrl?: string | null;
  countryCode?: string | null;
  rdvCount?: number;
  friendsCount?: number;
}
