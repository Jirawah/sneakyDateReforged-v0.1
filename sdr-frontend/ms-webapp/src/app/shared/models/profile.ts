export interface AggregatedProfile {
  userId: number;
  pseudo: string | null;
  avatarUrl: string | null;

  // (déjà présents côté back, utiles plus tard)
  nombreAmis?: number | null;
  nombreRDVs?: number | null;
  prochainRDV?: string | null; // ISO date-time
}
