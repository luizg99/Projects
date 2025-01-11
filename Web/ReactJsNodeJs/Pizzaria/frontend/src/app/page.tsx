import styles from './page.module.scss';
import Image from 'next/image';
import Link from 'next/link'
import { api } from '@/services/api'
import { redirect } from 'next/navigation'
import { cookies } from 'next/headers'

export default function Home() {

  async function handleLogin(formData: FormData){
    "use server"

    const email = formData.get("email")
    const password = formData.get("password")

    if(email === "" || password === ""){
      return;
    }

    try{

      const response = await api.post("/session", {
        email,
        password
      })

      if(!response.data.token){
        return;
      }

      console.log(response.data);

      const expressTime = 60 * 60 * 24 * 30 * 1000;
      const cookieStore = await cookies();
      
      cookieStore.set("session", response.data.token, {
        maxAge: expressTime,
        path: "/",
        httpOnly: false,
        secure: process.env.NODE_ENV === "production"
      })

    }catch(err){
      console.log(err);
      return;
    }

    redirect("/dashboard")

  }

  return (
    <>
      <div className={styles.containerCenter}>
        <Image
          src="/logo.svg" // Caminho direto para o arquivo na pasta public
          alt="teste logo"
          width={100} // Defina a largura da imagem
          height={100} // Defina a altura da imagem
        />

      <section className={styles.login}>
        <form action={handleLogin}>
            <input 
              type="email"
              required
              name="email"
              placeholder="Digite seu email..."
              className={styles.input}
            />

            <input 
              type="password"
              required
              name="password"
              placeholder="***********"
              className={styles.input}
            />

            <button type="submit">
              Acessar
            </button>
          </form>

          <Link href="/singup" className={styles.text}>
            Não possui uma conta? Cadastre-se
          </Link>

        </section>

      </div>
    </>
  );
}
