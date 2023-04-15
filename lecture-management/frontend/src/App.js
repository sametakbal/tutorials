import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Layout from './layout/Layout';
import User from './pages/User';
import Lecture from './pages/Lecture';

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path='/' element={<Layout />}>
            <Route index element={<User />} />
            <Route path="lectures" element={<Lecture />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  );
}

export default App;
