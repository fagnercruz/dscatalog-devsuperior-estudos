import './styles.css';
import { ReactComponent as SetaIcon } from 'assets/images/Seta.svg';

const ButtonIcon = () => {
  return (
    <div className="btn-container">
      <button className="btn btn-primary">
        <h6>inicie agora a sua busca</h6>
      </button>
      <div className="btn-icon-container">
        <SetaIcon />
      </div>
    </div>
  );
};

export default ButtonIcon;
