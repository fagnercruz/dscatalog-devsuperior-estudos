import './style.css';
import ProductImg from 'assets/images/pc.png';
import ProductPrice from 'components/ProductPrice';

const ProductCard = () => {
  return (
    <div className='base-card product-card'>
      <div className='card-top-container'>
        <img src={ProductImg} alt="imagem" />
      </div>
      <div className='card-bottom-container'>
        <h6>Nome do produto</h6>
        <ProductPrice />
      </div>
    </div>
  );
};

export default ProductCard;
