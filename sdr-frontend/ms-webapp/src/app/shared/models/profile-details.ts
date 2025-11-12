export interface ProfileDetails {
  discord: { pseudo: string | null; avatarUrl: string | null };
  steam:   { pseudo: string | null; avatarUrl: string | null };
  games:   { name: string; hours: number }[];
}
