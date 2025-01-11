import { cookies } from 'next/headers';

export async function getCookieServer() {
  const cookieStore = await cookies(); // Não use await aqui
  const token = cookieStore.get("session")?.value;

  return token || null;
}
