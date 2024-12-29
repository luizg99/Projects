import styles from './page.module.scss'
import logoImg from '/frontend/public/logo.svg'
import { Image } from 'next/image'

export default function Home() {
  return (
    <>
      <div className={styles.containerCenter}>
        <Image
          src={logoImg}   
          alt="teste logo"     
        />

      </div>
    </>
  );
}
