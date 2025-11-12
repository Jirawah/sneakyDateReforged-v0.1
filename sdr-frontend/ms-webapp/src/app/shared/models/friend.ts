export interface Friend {
  id: number | string;
  pseudo: string;
  avatarUrl?: string | null;
  status?: 'CONFIRME' | 'EN_ATTENTE' | 'REFUSE';
  online?: boolean;
}
