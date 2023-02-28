import './App.css';
import Layout from './layout/Layout';
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Lecture from './pages/Lecture';
import User from './pages/User';

function App() {
  return (
   <>
   <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<User />} />
          <Route path="lectures" element={<Lecture />} />
        </Route>
      </Routes>
    </BrowserRouter>
   </>
  );
}

export default App;
